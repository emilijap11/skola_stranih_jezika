package rs.fon.skolajezika.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Uplata extends BaseEntity {

    @Min(1)
    @Max(12)
    private int mesec;

    @Min(2020)
    private int godina;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal iznos;

    @NotNull
    private LocalDate datumUplate;

    @Enumerated(EnumType.STRING)
    @NotNull
    private StatusUplate status;

    @ManyToOne(fetch = FetchType.EAGER)
    private Ucenik ucenik;

    @ManyToOne(fetch = FetchType.EAGER)
    private Kurs kurs;

    protected Uplata() {
    }

    public Uplata(Ucenik ucenik, Kurs kurs, int mesec, int godina, BigDecimal iznos, LocalDate datumUplate) {
        uredi(ucenik, kurs, mesec, godina, iznos, datumUplate);
        this.status = StatusUplate.EVIDENTIRANA;
    }

    public void uredi(Ucenik ucenik, Kurs kurs, int mesec, int godina, BigDecimal iznos, LocalDate datumUplate) {
        this.ucenik = ucenik;
        this.kurs = kurs;
        this.mesec = mesec;
        this.godina = godina;
        this.iznos = iznos;
        this.datumUplate = datumUplate;
    }

    public int getMesec() {
        return mesec;
    }

    public int getGodina() {
        return godina;
    }

    public BigDecimal getIznos() {
        return iznos;
    }

    public LocalDate getDatumUplate() {
        return datumUplate;
    }

    public StatusUplate getStatus() {
        return status;
    }

    public Ucenik getUcenik() {
        return ucenik;
    }

    public Kurs getKurs() {
        return kurs;
    }
}
