package com.ichat.service;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.Serializable;

public class ByteArrayFile implements Serializable {

    private static final long serialVersionUID = 195749988571509758L;

    private byte[] fileBytes;
    private String name;
    private String id;

    public ByteArrayFile(String name, byte[] fileBytes) {
        this.name = name;
        this.fileBytes = fileBytes;
        this.id = RandomStringUtils.random(7, true, true);
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public ByteArrayFile setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
        return this;
    }

    public String getName() {
        return name;
    }

    public ByteArrayFile setName(String name) {
        this.name = name;
        return this;
    }

    public String getUniqueFileName() {
        return id + "_" + getName();
    }

    public String getId() {
        return id;
    }

    public ByteArrayFile setId(String id) {
        this.id = id;
        return this;
    }
}
