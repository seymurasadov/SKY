package com.sky;

import guru.nidi.codeassert.config.AnalyzerConfig;
import guru.nidi.codeassert.dependency.DependencyAnalyzer;
import org.junit.jupiter.api.Test;

import static guru.nidi.codeassert.junit.CodeAssertMatchers.hasNoCycles;
import static org.hamcrest.MatcherAssert.assertThat;

class DependencyTest {

  private final AnalyzerConfig config = AnalyzerConfig.gradle().mainAndTest();

  @Test
  void noCycles() {
    assertThat(new DependencyAnalyzer(config).analyze(), hasNoCycles());
  }

}