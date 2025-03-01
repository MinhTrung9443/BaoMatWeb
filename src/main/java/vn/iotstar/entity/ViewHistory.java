package vn.iotstar.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewHistory {
	
	@Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long viewHistoryId;

    @ManyToOne()
    @JoinColumn(name="userId", referencedColumnName="id")
    @JsonBackReference
    private Person user;
    
    @ManyToOne()
    @JoinColumn(name="productId")
    @JsonBackReference
    private Product product;
}
