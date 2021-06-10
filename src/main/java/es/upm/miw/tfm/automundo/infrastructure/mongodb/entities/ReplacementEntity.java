package es.upm.miw.tfm.automundo.infrastructure.mongodb.entities;

import es.upm.miw.tfm.automundo.domain.model.Replacement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document
public class ReplacementEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private String reference;
    private String name;
    private BigDecimal price;
    private String description;

    public Replacement toReplacement() {
        Replacement replacement = new Replacement();
        BeanUtils.copyProperties(this, replacement);
        return replacement;
    }
}
