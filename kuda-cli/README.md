# README

## CLI

```
Usage: kuda-cli [optional flags] [command] args
GLOBAL FLAGS
    -h|--help                              Display help
    -v|--verbose                           Make the operation more talkative

COMMANDS
    deploy {FLOW_FILE}                     Deploy flow
    flows                                  Show flows
    triggers {FLOW_NAME} {ENDPOINT_NAME}   Show triggers
```

## Config

```bash
$ cat ~/.kuda_config
URL: http://localhost:8080
```