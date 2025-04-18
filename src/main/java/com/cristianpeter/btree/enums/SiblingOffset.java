package com.cristianpeter.btree.enums;

import lombok.Getter;

@Getter
public enum SiblingOffset {
    LEFT_SIBLING(-1),
    RIGHT_SIBLING(1);

    private int offset;

    SiblingOffset(int offset) {
        this.offset = offset;
    }

}
