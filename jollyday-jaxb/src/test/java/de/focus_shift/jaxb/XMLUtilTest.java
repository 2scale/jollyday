package de.focus_shift.jaxb;

import de.focus_shift.jaxb.XMLUtil;
import de.focus_shift.spi.Configuration;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class XMLUtilTest {

  @Mock
  private XMLUtil.JAXBContextCreator contextCreator;
  @Mock
  private InputStream inputStream;

  @InjectMocks
  private final XMLUtil xmlUtil = new XMLUtil();

  @Test
  void testUnmarshallConfigurationNullCheck() {
    assertThatThrownBy(() -> xmlUtil.unmarshallConfiguration(null)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void testUnmarshallConfigurationException() throws IOException, JAXBException {
    when(contextCreator.create(eq("de.focus_shift.jaxb.mapping"), any(ClassLoader.class))).thenThrow(new JAXBException("")).thenThrow(new JAXBException(""));
    assertThatThrownBy(() -> xmlUtil.unmarshallConfiguration(inputStream)).isInstanceOf(IllegalStateException.class);
    verify(inputStream, never()).close();
  }

  @Test
  void testUnmarshallConfiguration() throws JAXBException {
    final JAXBContext ctx = mock(JAXBContext.class);
    final Unmarshaller unmarshaller = mock(Unmarshaller.class);
    @SuppressWarnings("unchecked") final JAXBElement<Configuration> element = mock(JAXBElement.class);
    when(contextCreator.create(eq("de.focus_shift.jaxb.mapping"), any(ClassLoader.class))).thenReturn(null).thenReturn(ctx);
    when(ctx.createUnmarshaller()).thenReturn(unmarshaller);
    when(unmarshaller.unmarshal(inputStream)).thenReturn(element);
    xmlUtil.unmarshallConfiguration(inputStream);
    verify(element).getValue();
  }
}
