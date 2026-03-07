package com.orbis.stream.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 512)
    private String streamUrl;

    @Column(nullable = false, length = 512)
    private String streamKey;

    private String platformStreamName;

    @ManyToOne
    private User user;
}
