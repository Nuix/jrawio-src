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
 * $Id: MRWHeaderProcessor.java 151 2008-09-13 15:13:22Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.raf;

import javax.annotation.Nonnull;
import java.io.IOException;
import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.HeaderProcessor;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id: MRWHeaderProcessor.java 151 2008-09-13 15:13:22Z fabriziogiudici $
 *
 ******************************************************************************/
public class RAFHeaderProcessor extends HeaderProcessor
  {
    private final static String CLASS = RAFHeaderProcessor.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);
    
    private final FujiRawData fujiRawData = new FujiRawData();
    
    /***************************************************************************
     * 
     * @param  iis  the input stream
     * 
     **************************************************************************/
    @Override
    public void process (@Nonnull final RAWImageInputStream iis) 
      throws IOException
      {        
        iis.setBaseOffset(0);
//        iis.seek(48);
        fujiRawData.load(iis, 0, iis.getByteOrder());
      }
    
    /***************************************************************************
     * 
     * 
     **************************************************************************/
    @Override
    public int getBaseOffset() 
      {
        return fujiRawData.getBaseOffset();
      }
    
    /***************************************************************************
     * 
     * 
     **************************************************************************/
    @Nonnull
    public FujiRawData getFujiRawData()
      {
        return fujiRawData;
      }
  }