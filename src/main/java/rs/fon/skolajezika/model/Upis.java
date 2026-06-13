package rs.fon.skolajezika.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
public class Upis extends BaseEntity {

    @NotNull
    private LocalDate datumPocetka;

    @Enumerated(EnumType.STRING)
    @NotNull
    private StatusUpisa status;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Ucenik ucenik;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Kurs kurs;

    @ManyToOne(fetch = FetchType.EAGER)
    private NastavnaGrupa grupa;

    protected Upis() {
    }

    public Upis(Ucenik ucenik, Kurs kurs, LocalDate datumPocetka, StatusUpisa status) {
        uredi(ucenik, kurs, datumPocetka);
        this.status = status;
    }

    public void uredi(Ucenik ucenik, Kurs kurs, LocalDate datumPocetka) {
        this.ucenik = ucenik;
        this.kurs = kurs;
        this.datumPocetka = datumPocetka;
    }

    public void dodeliGrupu(NastavnaGrupa grupa) {
        this.grupa = grupa;
        this.kurs = grupa.getKurs();
    }

    public void otkazi() {
        this.status = StatusUpisa.OTKAZAN;
    }

    public LocalDate getDatumPocetka() {
        return datumPocetka;
    }

    public StatusUpisa getStatus() {
        return status;
    }

    public Ucenik getUcenik() {
        return ucenik;
    }

    public Kurs getKurs() {
        return kurs;
    }

    public NastavnaGrupa getGrupa() { return grupa; }
}
