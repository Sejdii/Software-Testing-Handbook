package pl.sejdii.example.adapter.out.postgres;

import org.assertj.core.api.AbstractAssert;
import org.hibernate.stat.Statistics;

class StatisticAssertions extends AbstractAssert<StatisticAssertions, Statistics> {

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
}
