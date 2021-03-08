package com.springbatch.project.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@Setter
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class AddressDTO {

    @XmlAttribute(name = "street")
    private String street;

}
