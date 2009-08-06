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
package it.tidalwave.imageio.cr2;

import javax.annotation.Nonnull;
import javax.imageio.ImageReader;
import java.util.Collection;
import it.tidalwave.imageio.ExpectedResults;
import it.tidalwave.imageio.NewImageReaderTestSupport;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class CR2ImageReaderImageTest extends NewImageReaderTestSupport
  {
    public CR2ImageReaderImageTest (final @Nonnull ExpectedResults expectedResults)
      {
        super(expectedResults);
      }

    @Nonnull
    @Parameters
    public static Collection<Object[]> expectedResults()
      {
        return fixed
          (
            // EOS20D v1.1.0
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/canon/EOS20D/1.1.0/_MG_8587.CR2").
                            image(3504, 2336, 3, 16, "992a061e12ac559e49e040344a9e3bb4").
                            thumbnail(1536, 1024).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            extra(new ExpectedResults.Extra()
                              {
                                public void run (final @Nonnull ImageReader ir)
                                  throws Exception
                                  {
                                    final CR2Metadata metadata = (CR2Metadata)ir.getImageMetadata(0);
                                    assertNotNull(metadata);
                                  }
                              }),
            // EOS20D v2.0.3
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/canon/EOS20D/2.0.3/XXXX0000.CR2").
                            image(3504, 2336, 3, 16, "d15a94cfc3615c0a399696f1af2cdbd1").
                            thumbnail(1536, 1024).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            extra(new ExpectedResults.Extra()
                              {
                                public void run (final @Nonnull ImageReader ir)
                                  throws Exception
                                  {
                                    final CR2Metadata metadata = (CR2Metadata)ir.getImageMetadata(0);
                                    assertNotNull(metadata);
                                  }
                              }),
            // EOS30D v1.0.4
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/canon/EOS30D/1.0.4/_MG_8882.CR2").
                            image(3504, 2336, 3, 16, "67872b80eca784066a997f0bd6d00d29").
                            thumbnail(1728, 1152).
                            thumbnail(160, 120).
                            thumbnail(384, 256).
                            extra(new ExpectedResults.Extra()
                              {
                                public void run (final @Nonnull ImageReader ir)
                                  throws Exception
                                  {
                                    final CR2Metadata metadata = (CR2Metadata)ir.getImageMetadata(0);
                                    assertNotNull(metadata);
                                  }
                              }),
            // EOS40D v1.0.3
            ExpectedResults.create("http://s179771984.onlinehome.us/RAWpository/images/canon/EOS40D/1.0.3/img_0003.cr2").
                            image(3888, 2592, 3, 16, "1ea35bbc10cf900fca0457ea531226d5").
                            thumbnail(1936, 1288).
                            thumbnail(160, 120).
                            thumbnail(486, 324).
                            extra(new ExpectedResults.Extra()
                              {
                                public void run (final @Nonnull ImageReader ir)
                                  throws Exception
                                  {
                                    final CR2Metadata metadata = (CR2Metadata)ir.getImageMetadata(0);
                                    assertNotNull(metadata);
                                  }
                              })
          );
      }
  }

/*
        final BufferedImage image = assertLoadImage(ir, 3596, 2360, 3, 16);

        final BufferedImage image = assertLoadImage(ir, 3596, 2360, 3, 16);

        final BufferedImage image = assertLoadImage(ir, 3596, 2360, 3, 16);

        final BufferedImage image = assertLoadImage(ir, 3944, 2622, 3, 16);

 */