package net.contextfw.web.commons.cloud.storage;

import java.util.ArrayList;
import java.util.List;


public class PageScoped1 {

    private String msg;
    
    private List<String> items = new ArrayList<String>();

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public void addItem(String item) {
        items.add(item);
    }
    
    public String getItem(int index) {
        return items.get(index);
    }
    
}
