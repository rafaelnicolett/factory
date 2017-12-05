package pl.com.bottega.factory.demand.forecasting;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.com.bottega.tools.TechnicalId;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity(name = "Demand")
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class DemandEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToOne
    ProductDemandEntity product;
    @Column
    LocalDate date;

    @Column
    Long level;
    @Column
    Demand.Schema schema;

    @Column
    Long adjustmentLevel;
    @Column
    Demand.Schema adjustmentSchema;
    @Column
    boolean adjustmentStrong;

    DemandEntity(ProductDemandEntity product, LocalDate date) {
        this.product = product;
        this.date = date;
    }

    static Demand getDemand(DemandEntity entity) {
        return entity == null || entity.level == null
                ? null
                : Demand.of(entity.level, entity.schema);
    }

    static Adjustment getAdjustment(DemandEntity entity) {
        return entity == null || entity.adjustmentLevel == null
                ? null
                : new Adjustment(
                Demand.of(entity.adjustmentLevel, entity.adjustmentSchema),
                entity.adjustmentStrong
        );
    }

    void setDemand(Demand demand) {
        if (demand == null) {
            setLevel(null);
            setSchema(null);
        } else {
            setLevel(demand.getLevel());
            setSchema(demand.getSchema());
        }
    }

    void setAdjustment(Adjustment adjustment) {
        if (adjustment == null) {
            setAdjustmentLevel(null);
            setAdjustmentSchema(null);
            setAdjustmentStrong(false);
        } else {
            setAdjustmentLevel(adjustment.getDemand().getLevel());
            setAdjustmentSchema(adjustment.getDemand().getSchema());
            setAdjustmentStrong(adjustment.isStrong());
        }
    }

    DemandEntityId createId() {
        return new DemandEntityId(product.getRefNo(), date, id);
    }

    @Getter
    static class DemandEntityId extends DailyId implements TechnicalId {

        Long id;

        DemandEntityId(String refNo, LocalDate date) {
            super(refNo, date);
        }

        DemandEntityId(String refNo, LocalDate date, Long id) {
            super(refNo, date);
            this.id = id;
        }
    }
}