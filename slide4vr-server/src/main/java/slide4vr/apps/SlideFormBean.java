/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slide4vr.apps;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

/**
 *
 * @author koduki
 */
public class SlideFormBean {

    @FormParam("title")
    @PartType(MediaType.TEXT_PLAIN)
    private String title = null;

    @FormParam("slide")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    private byte[] slide = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getSlide() {
        return slide;
    }

    public void setSlide(byte[] slide) {
        this.slide = slide;
    }

}
