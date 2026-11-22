package io.github.fiftieshousewife.undertest.techniques.slices;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** One delivery, as the database keeps it: its reference, whose it is, the day it is due, and whether it came. */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Delivery {

    @Id
    @GeneratedValue
    private Long id;
    private String reference;
    private String customer;
    private LocalDate dueOn;
    private boolean delivered;
}
