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
public class Isplata extends BaseEntity {

    @Min(1)
    @Max(12)
    private int mesec;

    @Min(2020)
    private int godina;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal iznos;

    @NotNull
    private LocalDate datumIsplate;

    @Enumerated(EnumType.STRING)
    @NotNull
    private StatusIsplate status;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Profesor profesor;

    protected Isplata() {
    }

    public Isplata(Profesor profesor, int mesec, int godina, BigDecimal iznos, LocalDate datumIsplate) {
        uredi(profesor, mesec, godina, iznos, datumIsplate);
        this.status = StatusIsplate.EVIDENTIRANA;
    }

    public void uredi(Profesor profesor, int mesec, int godina, BigDecimal iznos, LocalDate datumIsplate) {
        this.profesor = profesor;
        this.mesec = mesec;
        this.godina = godina;
        this.iznos = iznos;
        this.datumIsplate = datumIsplate;
    }

    public int getMesec() { return mesec; }
    public int getGodina() { return godina; }
    public BigDecimal getIznos() { return iznos; }
    public LocalDate getDatumIsplate() { return datumIsplate; }
    public StatusIsplate getStatus() { return status; }
    public Profesor getProfesor() { return profesor; }
}
