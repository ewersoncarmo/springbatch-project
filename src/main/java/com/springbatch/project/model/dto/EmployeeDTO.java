package com.springbatch.project.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;

@Setter
@Getter
@XmlRootElement(name="employee")
@XmlAccessorType(XmlAccessType.FIELD)
public class EmployeeDTO {

    @XmlAttribute(name = "id")
    private Integer id;

    @XmlAttribute(name = "firstName")
    private String firstName;

    @XmlAttribute(name = "lastName")
    private String lastName;

    @XmlElement(name = "address", type = AddressDTO.class)
    private AddressDTO address;

}
