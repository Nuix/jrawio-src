/*******************************************************************************
 *
 * jrawio - a Java(TM) ImageIO API Spi Provider for RAW files
 * ==========================================================
 *
 * Copyright (C) 2003-2008 by Fabrizio Giudici
 * Project home page: http://jrawio.tidalwave.it
 *
 *******************************************************************************
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
 *******************************************************************************
 *
 * $Id: RasterReaderCoolpix.java 58 2008-08-22 19:17:28Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.nef;

import javax.annotation.Nonnegative;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: RasterReaderCoolpix.java 58 2008-08-22 19:17:28Z fabriziogiudici $
 *
 ******************************************************************************/
public class RasterReaderCoolpix extends NEFRasterReader
  {
    /*******************************************************************************
     * 
     * @inheritDoc
     * 
     * Coolpix rasters are interlaced.
     * 
     *******************************************************************************/
    @Override
    @Nonnegative
    protected int getRow (@Nonnegative final int y, @Nonnegative final int height)
      {
        return (y < (height / 2)) ? (y * 2) : ((y - height / 2) * 2 + 1);
      }
  }