package com.example.diprivi.g_hack;


import java.util.List;

public class EntireBody {
    List<ItemList> items;
    int res_code;


    public EntireBody(List<ItemList> items, int res_code) {
        this.items = items;
        this.res_code = res_code;
    }

    public List<ItemList> getItems() {
        return items;
    }

    public int getRes_code() {
        return res_code;
    }
}
