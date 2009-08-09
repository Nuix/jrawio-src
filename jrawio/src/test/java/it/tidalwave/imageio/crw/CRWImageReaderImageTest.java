/***********************************************************************************************************************
 *
 * jrawio - a Java(TM) Image I/O SPI Provider for Camera Raw files
 * ===============================================================
 *
 * Copyright (C) 2003-2009 by Tidalwave s.a.s. (http://www.tidalwave.it)
 * http://jrawio.tidalwave.it
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 *
 ***********************************************************************************************************************
 *
 * $Id$
 *
 **********************************************************************************************************************/
package it.tidalwave.imageio.crw;

import javax.annotation.Nonnull;
import java.util.Collection;
import it.tidalwave.imageio.ExpectedResults;
import it.tidalwave.imageio.NewImageReaderTestSupport;
import org.junit.runners.Parameterized.Parameters;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class CRWImageReaderImageTest extends NewImageReaderTestSupport
  {
    public CRWImageReaderImageTest (final @Nonnull ExpectedResults expectedResults)
      {
        super(expectedResults);
      }

    @Nonnull
    @Parameters
    public static Collection<Object[]> expectedResults()
      {
        return fixed
          (
            // EOS300D v1.1.1
            ExpectedResults.create("https://imaging.dev.java.net/nonav/TestSets/esordini/Canon/EOS300D/CRW/100_0056.CRW").
                            image(3152, 2068, 3, 16, "b499a77c82e5289d043e2f330c6fffba").
                            thumbnail(2048, 1360).
                            issues("JRW-200").
                            metadata("metadata.width", 3152).
                            metadata("metadata.height", 2068).
                            metadata("metadata.fileNumber", 1000056).
                            metadata("metadata.imageWidth", 3072).
                            metadata("metadata.imageHeight", 2048).
                            metadata("metadata.thumbnailWidth", 2048).
                            metadata("metadata.thumbnailHeight", 1360).
                            metadata("metadata.pixelAspectRatio", 1.0f).
                            metadata("metadata.rotation", 0).
                            metadata("metadata.componentBitDepth", 8).
                            metadata("metadata.colorBitDepth", 24).
                            metadata("metadata.colorBW", 257).
//                            metadata("metadata.timeStampAsDate", 0).
                            metadata("metadata.baseISO", 100).
                            metadata("metadata.firmwareVersion", "Firmware Version 1.1.1").
                            metadata("metadata.model", "Canon EOS 300D DIGITAL").
                            metadata("metadata.sensorWidth", 3152).
                            metadata("metadata.sensorHeight", 2068).
                            metadata("metadata.sensorLeftBorder", 72).
                            metadata("metadata.sensorTopBorder", 16).
                            metadata("metadata.sensorRightBorder", 3143).
                            metadata("metadata.sensorBottomBorder", 2063).
                            metadata("metadata.focalLength", 28.0f).
                            metadata("metadata.serialNumber", 1330526302).
                            metadata("metadata.decoderTable", new int[] {0, 0, 0x202, 0x52B336}).
                            metadata("metadata.whiteBalance", 0).
                            metadata("metadata.whiteBalanceAsString", "auto").
                            metadata("metadata.RBCoefficients", new double[] { 1.9963898916967509, 1.1778846153846154}).
                            metadata("metadata.colorTemperatureAvailable", true).
                            metadata("metadata.colorTemperature", 5200)
          );
      }
  }
