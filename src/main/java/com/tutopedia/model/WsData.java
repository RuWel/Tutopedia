package com.tutopedia.model;

import lombok.Getter;
import lombok.Setter;

public class WsData {
	@Getter @Setter
    String data;

    public WsData(String data) {
        this.data = data;
    }
}
