package pl.sejdii.example.adapter.out.postgres;

import org.assertj.core.api.AbstractAssert;
import org.hibernate.stat.Statistics;

public class StatisticAssertions extends AbstractAssert<StatisticAssertions, Statistics> {

  protected StatisticAssertions(Statistics statistics) {
    super(statistics, StatisticAssertions.class);
  }

  public static StatisticAssertions assertThat(Statistics statistics) {
    return new StatisticAssertions(statistics);
  }

  public StatisticAssertions hasInsertCount(int expected) {
    isNotNull();
    if (actual.getEntityInsertCount() != expected) {
      failWithMessage(
          "Expected entity insert count to be <%s> but was <%s>",
          expected, actual.getEntityInsertCount());
    }
    return this;
  }

  public StatisticAssertions hasQueryCount(int expected) {
    isNotNull();
    if (actual.getPrepareStatementCount() != expected) {
      failWithMessage(
          "Expected query count to be <%s> but was <%s>",
          expected, actual.getPrepareStatementCount());
    }
    return this;
  }

  public StatisticAssertions hasUpdateCount(int expected) {
    isNotNull();
    if (actual.getEntityUpdateCount() != expected) {
      failWithMessage(
          "Expected entity update count to be <%s> but was <%s>",
          expected, actual.getEntityUpdateCount());
    }
    return this;
  }
}
