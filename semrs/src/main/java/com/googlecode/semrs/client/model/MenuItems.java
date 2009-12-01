package com.googlecode.semrs.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MenuItems implements IsSerializable{

        public String[][] data;
        public int totalRecords;

        public MenuItems(String[][] data, int totalRecords) {
            this.data = data;
            this.totalRecords = totalRecords;
        }

        public MenuItems() {
        }
    }

