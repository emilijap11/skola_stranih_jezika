package rs.fon.skolajezika.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class EvidencijaTermina extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @NotNull
    private StatusEvidencije status;

    private String napomena;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Upis upis;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private TerminCasa terminCasa;

    protected EvidencijaTermina() {
    }

    public EvidencijaTermina(Upis upis, TerminCasa terminCasa, StatusEvidencije status, String napomena) {
        this.upis = upis;
        this.terminCasa = terminCasa;
        this.status = status;
        this.napomena = napomena;
    }

    public StatusEvidencije getStatus() {
        return status;
    }

    public String getNapomena() {
        return napomena;
    }

    public Upis getUpis() {
        return upis;
    }

    public TerminCasa getTerminCasa() {
        return terminCasa;
    }
}
