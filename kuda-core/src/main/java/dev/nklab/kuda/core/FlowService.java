/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dev.nklab.kuda.core;

import static dev.nklab.jl2.Extentions.$;
import static dev.nklab.jl2.web.gcp.datastore.Extentions.noindex;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityValue;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.ListValue;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Query;
import dev.nklab.jl2.collections.CollectionUtils;
import dev.nklab.jl2.web.mp.Config;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author koduki
 */
@ApplicationScoped
public class FlowService {

    private Map<String, Map<String, Map<String, Map<String, String>>>> myTriggers = null;

    public List<String> list() {
        var datastore = DatastoreOptions.getDefaultInstance().getService();
        var gql = "SELECT distinct name FROM Flow";
        var q = Query.newGqlQueryBuilder(Query.ResultType.PROJECTION_ENTITY, gql)
                .setAllowLiteral(true)
                .build();

        return CollectionUtils.toStream(datastore.run(q))
                .map(x -> x.getString("name"))
                .collect(Collectors.toList());
    }

    public Map<String, Object> store(final InputStream input) {
        var datastore = DatastoreOptions.getDefaultInstance().getService();
        var txn = datastore.newTransaction();
        var yaml = new Yaml();
        var rootConfig = (Map) yaml.load(input);

        var flowName = (String) rootConfig.get("name");
        var version = flowName + "_" + System.currentTimeMillis();
        var flowKey = datastore.newKeyFactory().setKind("Flow").newKey();
        var flow = Entity.newBuilder(flowKey)
                .set("name", flowName)
                .set("version", version)
                .build();
        var flowKeyId = txn.put(flow).getKey().getId();

        var endpoints = buildEndpoints(datastore, rootConfig, flowKeyId);
        endpoints.forEach(e -> txn.put(e));
        txn.commit();

        return Map.of(flowName, flowKeyId);
    }

    public Map<String, Map<String, String>> getTriggers(String flowName, String endpointName) {
        var isLocalFlow = Config.get("kuda.core.localflow.enable", false);
        var localFlowPath = Config.get("kuda.core.localflow.path", "");

        if (myTriggers == null) {
            myTriggers = new HashMap<>();
        }
        if (!myTriggers.containsKey(flowName)) {
            myTriggers.put(flowName, new HashMap<>());
        }

        if (!myTriggers.get(flowName).containsKey(endpointName)) {
            var t = (isLocalFlow)
                    ? getTriggersFromLocal(localFlowPath, endpointName)
                    : getTriggersFromDb(flowName, endpointName);
            myTriggers.get(flowName).put(endpointName, t);
        }
        System.out.println(myTriggers);
        return myTriggers.get(flowName).get(endpointName);
    }

    Map<String, Map<String, String>> getTriggersFromLocal(String localFlowPath, String endpointName) {
        try ( var input = new FileInputStream(localFlowPath)) {
            var yaml = new Yaml();
            var rootConfig = (Map) yaml.load(input);
            var endpointsConfig = ((List<Map>) rootConfig.get("endpoints"));

            var urls = endpointsConfig.stream().collect(Collectors.toMap(
                    x -> x.get("name"),
                    x -> x.get("url")));
            var triggers = endpointsConfig.stream()
                    .filter(x -> x.get("name").equals(endpointName))
                    .filter(x -> x.containsKey("triggers"))
                    .flatMap(x -> ((List<Map>) x.get("triggers")).stream().map(t -> {
                return Map.of(
                        (String) t.getOrDefault("condition", "always"),
                        Map.of("trigger-url", (String) urls.get(t.get("name")), "version", "-")
                );
            })).collect(Collectors.toMap(
                    x -> x.keySet().iterator().next(),
                    x -> x.values().iterator().next()
            ));

            return triggers;

        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

    }

    Map<String, Map<String, String>> getTriggersFromDb(String flowName, String endpointName) {
        var datastore = DatastoreOptions.getDefaultInstance().getService();
        var flowId = getFlowId(datastore, flowName);
        var urls = getUrls(datastore, flowId);
        var gql = "SELECT * FROM Endpoint WHERE __key__ HAS ANCESTOR KEY(Flow, " + flowId + ") and name=@endpointName";
        var q = Query.newGqlQueryBuilder(Query.ResultType.ENTITY, gql)
                .setBinding("endpointName", endpointName)
                .setAllowLiteral(true)
                .build();
        var result = CollectionUtils.toStream(datastore.run(q))
                .filter(e -> e.contains("trigger"))
                .flatMap(e -> {
                    return e.getList("trigger").stream().map(x -> {
                        var trigger = (FullEntity) x.get();
                        var key = trigger.contains("condition") ? trigger.getString("condition") : "always";
                        var value = trigger.getString("target");
                        return $(key, value);
                    });
                })
                .collect(Collectors.toMap(
                        x -> x._1(),
                        x -> Map.of("trigger-url", urls.get(x._2()), "version", "-"))
                );
        return result;
    }

    private Stream<FullEntity<IncompleteKey>> buildEndpoints(Datastore datastore, Map rootConfig, long flowKeyId) {
        return ((List<Map>) rootConfig.get("endpoints")).stream().map(endpointConfig -> {
            var key = datastore.newKeyFactory()
                    .addAncestors(PathElement.of("Flow", flowKeyId))
                    .setKind("Endpoint")
                    .newKey();

            var builder = Entity.newBuilder(key)
                    .set("name", (String) endpointConfig.get("name"))
                    .set("url", noindex((String) endpointConfig.get("url")))
                    .set("created_at", noindex(Timestamp.now()));
            if (endpointConfig.containsKey("triggers")) {
                builder.set("trigger", new ListValue(buildTriggers(datastore, endpointConfig)));
            }
            return builder.build();
        });
    }

    private List<EntityValue> buildTriggers(Datastore datastore, Map endpointConfig) {
        return ((List<Map<String, String>>) endpointConfig.get("triggers")).stream()
                .map(triggerConfig -> {
                    var key = datastore.newKeyFactory().setKind("Trigger").newKey();
                    var builder = Entity.newBuilder(key)
                            .set("target", triggerConfig.get("name"))
                            .set("created_at", noindex(Timestamp.now()));
                    if (triggerConfig.containsKey("condition")) {
                        builder.set("condition", triggerConfig.get("condition"));
                    }
                    return builder.build();

                })
                .map(x -> new EntityValue(x))
                .collect(Collectors.toList());
    }

    private Long getFlowId(Datastore datastore, String flowName) {
        var gql = "SELECT * FROM Flow WHERE name=@flowName";
        var q = Query.newGqlQueryBuilder(Query.ResultType.ENTITY, gql)
                .setBinding("flowName", flowName)
                .setAllowLiteral(true)
                .build();
        var flowId = CollectionUtils.toStream(datastore.run(q))
                .sorted((x, y) -> y.getString("version").compareTo(x.getString("version")))
                .map(x -> x.getKey().getId())
                .findFirst().get();

        return flowId;
    }

    private Map<String, String> getUrls(Datastore datastore, long flowId) {
        var gql = "SELECT * FROM Endpoint WHERE __key__ HAS ANCESTOR KEY(Flow, " + flowId + ")";
        var q = Query.newGqlQueryBuilder(Query.ResultType.ENTITY, gql)
                .setAllowLiteral(true)
                .build();

        return CollectionUtils.toStream(datastore.run(q))
                .collect(Collectors.toMap(
                        x -> x.getString("name"),
                        x -> x.getString("url"))
                );
    }
}
