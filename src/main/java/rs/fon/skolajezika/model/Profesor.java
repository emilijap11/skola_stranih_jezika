package rs.fon.skolajezika.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Profesor extends BaseEntity {

    @NotBlank
    private String ime;

    @NotBlank
    private String prezime;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "profesor_jezici", joinColumns = @JoinColumn(name = "profesor_id"))
    @Enumerated(EnumType.STRING)
    @NotEmpty
    private Set<Jezik> jezici = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "profesor_nivoi", joinColumns = @JoinColumn(name = "profesor_id"))
    @Enumerated(EnumType.STRING)
    @NotEmpty
    private Set<Nivo> nivoi = new HashSet<>();

    @NotBlank
    private String brojLicence;

    private LocalDate datumAngazovanja;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false)
    private BigDecimal mesecnaNaknada;

    protected Profesor() {
    }

    public Profesor(String ime, String prezime, Set<Jezik> jezici, Set<Nivo> nivoi, String brojLicence) {
        this(ime, prezime, jezici, nivoi, brojLicence, LocalDate.now(), BigDecimal.ZERO);
    }

    public Profesor(String ime, String prezime, Set<Jezik> jezici, Set<Nivo> nivoi, String brojLicence,
                    LocalDate datumAngazovanja, BigDecimal mesecnaNaknada) {
        uredi(ime, prezime, jezici, nivoi, brojLicence, datumAngazovanja, mesecnaNaknada);
    }

    public void uredi(String ime, String prezime, Set<Jezik> jezici, Set<Nivo> nivoi, String brojLicence,
                      LocalDate datumAngazovanja, BigDecimal mesecnaNaknada) {
        this.ime = ime;
        this.prezime = prezime;
        this.jezici = new HashSet<>(jezici);
        this.nivoi = new HashSet<>(nivoi);
        this.brojLicence = brojLicence;
        this.datumAngazovanja = datumAngazovanja;
        this.mesecnaNaknada = mesecnaNaknada;
    }

    public String getIme() {
        return ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public Set<Jezik> getJezici() {
        return jezici;
    }

    public Set<Nivo> getNivoi() {
        return nivoi;
    }

    public String getBrojLicence() {
        return brojLicence;
    }

    public LocalDate getDatumAngazovanja() {
        return datumAngazovanja == null ? LocalDate.now() : datumAngazovanja;
    }

    public BigDecimal getMesecnaNaknada() {
        return mesecnaNaknada;
    }

    public boolean predaje(Jezik jezik, Nivo nivo) {
        return jezici.contains(jezik) && nivoi.contains(nivo);
    }
}
