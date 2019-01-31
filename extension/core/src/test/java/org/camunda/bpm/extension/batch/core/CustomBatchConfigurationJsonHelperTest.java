package org.camunda.bpm.extension.batch.core;

import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomBatchConfigurationJsonHelperTest {

  @Mock
  private ProcessEngineConfigurationImpl engineConfiguration;

  @Mock
  private CommandContext commandContext;

  private CustomBatchConfigurationHelper configurationHelper;

  @Before
  public void setUp() {
    configurationHelper = CustomBatchConfigurationJsonHelper.of(CustomBatchConfigurationJsonConverter.of());
    when(engineConfiguration.getDefaultCharset()).thenReturn(Charset.defaultCharset());
    Context.setProcessEngineConfiguration(engineConfiguration);
    Context.setCommandContext(commandContext);
  }

  @Test
  public void convert_to_bytearray_and_back_for_string_config() {
    final List<String> data = Arrays.asList("Test", "Test2");
    final CustomBatchConfiguration<String> configuration = new CustomBatchConfiguration<>(data);

    final byte[] bytes = configurationHelper.writeConfiguration(configuration);
    assertThat(bytes).isNotEmpty();

    final CustomBatchConfiguration<String> readConfiguration = configurationHelper.readConfiguration(bytes);
    assertThat(readConfiguration).isNotNull();
    assertThat(readConfiguration.getData()).isNotEmpty();
    assertThat(readConfiguration.getData()).hasSize(2);
    assertThat(readConfiguration.getData().get(1)).isEqualTo("Test2");
  }

  @Test
  public void convert_to_bytearray_and_back_for_Object_config() {
    final List<TestConfigObject> data = Arrays.asList(new TestConfigObject("test"),
      new TestConfigObject("test2"), new TestConfigObject("test3"));
    final CustomBatchConfiguration<TestConfigObject> configuration = new CustomBatchConfiguration<>(data);

    final byte[] bytes = configurationHelper.writeConfiguration(configuration);
    assertThat(bytes).isNotEmpty();

    final CustomBatchConfiguration<TestConfigObject> readConfiguration = configurationHelper.readConfiguration(bytes);
    assertThat(readConfiguration).isNotNull();
    assertThat(readConfiguration.getData()).isNotEmpty();
    assertThat(readConfiguration.getData()).hasSize(3);
    assertThat(readConfiguration.getData().get(2).text).isEqualTo("test3");
  }

  @Test
  public void convert_to_bytearray_and_back_for_exclusive_false() {
    final List<String> data = Arrays.asList("Test", "Test2");
    final CustomBatchConfiguration configuration = new CustomBatchConfiguration<>(data, false);

    final byte[] bytes = configurationHelper.writeConfiguration(configuration);
    assertThat(bytes).isNotEmpty();

    final CustomBatchConfiguration<String> readConfiguration = configurationHelper.readConfiguration(bytes);
    assertThat(readConfiguration).isNotNull();
    assertThat(readConfiguration.isExclusive()).isFalse();
  }

  static class TestConfigObject implements Serializable {
    private static final long serialVersionUID = -6177454776892282471L;
    public String text;
    public Integer something = 1;

    public TestConfigObject(final String text) {
      this.text = text;
    }
  }
}
