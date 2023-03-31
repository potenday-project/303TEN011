package com.beside.ten011.archive.entity;

import com.beside.ten011.user.entity.User;
import com.beside.ten011.util.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Archive extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    private String author;

    @Lob
    private String content;

    private String imageSize;

    private String backgroundColor;

    private String fontStyle;

    private String fontColor;

    public void modify(String title, String author, String content, String imageSize,
                       String backgroundColor, String fontStyle, String fontColor) {
        this.title = StringUtils.defaultIfBlank(title, this.title);
        this.author = StringUtils.defaultIfBlank(author, this.author);
        this.content = StringUtils.defaultIfBlank(content, this.content);
        this.imageSize = StringUtils.defaultIfBlank(imageSize, this.imageSize);
        this.backgroundColor = StringUtils.defaultIfBlank(backgroundColor, this.backgroundColor);
        this.fontStyle = ObjectUtils.defaultIfNull(fontStyle, this.fontStyle);
        this.fontColor = StringUtils.defaultIfBlank(fontColor, this.fontColor);
    }
}
