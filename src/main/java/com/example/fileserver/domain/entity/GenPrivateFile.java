package com.example.fileserver.domain.entity;

import com.example.fileserver.converter.BooleanToIntegerConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "gen_private_file")
public class GenPrivateFile implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "object_type")
    private String objectType;
    @Column(name = "object_id")
    private Long objectId;
    @Column(name = "file_name")
    private String fileName;
    @Column(name = "disk_path")
    private String diskPath;
    @Column(name = "web_path")
    private String webPath;
    @Column(name = "is_deleted")
    @Convert(converter = BooleanToIntegerConverter.class)
    private Boolean isDeleted;
    @Column(name = "mime_type")
    private String mimeType;
    @Column(name = "file_extension")
    private String fileExtension;
    @Column(name = "file_size_mb")
    private Double fileSizeMb;
    @Column(name = "thumbnail_bas64")
    private String thumbnailBas64;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    @Column(name = "created_by")
    private Long createdBy;
    @Column(name = "user_id")
    private String userId;
}