package com.edstem.project.model;

import com.edstem.project.converter.MapConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "action_type")
    private String actionType;
    @Column(name = "action_value")
    @Convert(converter = MapConverter.class)
    private Map<String , String> actionValue = new HashMap<>();

}