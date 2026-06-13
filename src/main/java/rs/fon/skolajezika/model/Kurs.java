package rs.fon.skolajezika.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
public class Kurs extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @NotNull
    private Jezik jezik;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Nivo nivo;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TipKursa tip;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal cenaMesecno;

    protected Kurs() {
    }

    public Kurs(Jezik jezik, Nivo nivo, TipKursa tip, BigDecimal cenaMesecno) {
        uredi(jezik, nivo, tip, cenaMesecno);
    }

    public void uredi(Jezik jezik, Nivo nivo, TipKursa tip, BigDecimal cenaMesecno) {
        this.jezik = jezik;
        this.nivo = nivo;
        this.tip = tip;
        this.cenaMesecno = cenaMesecno;
    }

    public Jezik getJezik() {
        return jezik;
    }

    public Nivo getNivo() {
        return nivo;
    }

    public TipKursa getTip() {
        return tip;
    }

    public BigDecimal getCenaMesecno() {
        return cenaMesecno;
    }
}
