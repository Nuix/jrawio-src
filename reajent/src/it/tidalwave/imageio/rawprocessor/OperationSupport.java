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
 * $Id: OperationSupport.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
package it.tidalwave.imageio.rawprocessor;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;
import java.io.InputStream;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.color.ICC_ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;

/*******************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version CVS $Id: OperationSupport.java 55 2008-08-21 19:43:51Z fabriziogiudici $
 *
 ******************************************************************************/
public abstract class OperationSupport implements Operation
  {
    private static Map propertyMap = new HashMap();
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    protected static Logger getLogger (Class clazz)
      {
        return Logger.getLogger(getQualifiedName(clazz));
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    protected static String getQualifiedName (Class clazz)
      {
        return clazz.getPackage().getName() + "." + clazz.getName();
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    public void init (RAWImage image) throws Exception
      {        
      }
    
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    protected static Properties getProperties (Class clazz)
      {
        String name = clazz.getName();
        int i = name.lastIndexOf('.');

        if (i >= 0)
          {
            name = name.substring(i + 1);
          }

        return getProperties(clazz, name + ".properties");
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    private static Properties getProperties (Class clazz, String resName)
      {
        String key = clazz+ ":" + resName;
        Properties properties = (Properties)propertyMap.get(key);

        if (properties == null)
          {
            InputStream[] is = getResourceInputStream(clazz, resName);

            properties = new Properties();

            if (is != null)
              {
                for (int i = 0; i < is.length; i++)
                  {

                    try
                      {
                        properties.load(is[i]);
                        is[i].close();
                      }

                    catch (IOException e)
                      {
                        throw new RuntimeException(e);
                      }
                  }

                propertyMap.put(key, properties);
              }
          }

        return properties;
      }

    /*******************************************************************************
     * 
     * @param resName
     * @return
     * 
     *******************************************************************************/
    public static InputStream[] getResourceInputStream (Class clazz, String resName)
      {
        List temp = new ArrayList();

        for (; clazz != null; clazz = clazz.getSuperclass())
          {
            String string = "/" + clazz.getPackage().getName().replace('.', '/') + "/" + resName;
            InputStream is = clazz.getResourceAsStream(string);

            if (is != null)
              {
                System.err.println("OperationSpiSupport.getProperties() from " + string);
                temp.add(is);
              }
          }

        return (InputStream[])temp.toArray(new InputStream[0]);
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    protected static String toString (
        int[] array,
        int   radix)
      {
        StringBuffer buffer = new StringBuffer("");

        for (int i = 0; i < array.length; i++)
          {
            buffer.append(Integer.toString(array[i], radix));

            if (i < (array.length - 1))
              {
                buffer.append(",");
              }
          }

        return buffer.toString();
      }

    /**
     * @param image
     */
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    protected static void logImage (Logger logger, String prefix, RenderedImage image)
      {
        ColorModel colorModel = image.getColorModel();
        logger.fine(prefix + ".class:          " + image.getClass().getName());
        logger.fine(prefix + ".sampleModel:    " + toString(image.getSampleModel()));
        logger.fine(prefix + ".colorModel:     " + colorModel.getClass().getName() + " : " + colorModel);
        logger.fine(prefix + ".colorSpace:     " + toString(colorModel.getColorSpace()));

        //      log.debug(">>>> iccProfile is now: " + getICCProfileName(bufferedImage));
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    private static String toString (SampleModel sampleModel)
      {
        if (sampleModel instanceof SinglePixelPackedSampleModel)
          {
            return toString((SinglePixelPackedSampleModel)sampleModel);
          }

        else if (sampleModel instanceof PixelInterleavedSampleModel)
          {
            return toString((PixelInterleavedSampleModel)sampleModel);
          }

        else
          {
            return sampleModel.toString();
          }
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    private static String toString (ColorSpace colorSpace)
      {
       if (colorSpace instanceof ICC_ColorSpace)
          {
            return toString((ICC_ColorSpace)colorSpace);
          }

        else
          {
            return colorSpace.toString();
          }
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    private static String toString (ICC_ColorSpace colorSpace)
      {
        StringBuffer buffer = new StringBuffer("");
        buffer.append(colorSpace.getClass().getName());
        buffer.append("[type: ");
        buffer.append(colorSpace.getType());
        buffer.append(", profile name: ");
        buffer.append(getICCProfileName(colorSpace.getProfile()));
        buffer.append("]");

        return buffer.toString();
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    private static String toString (SinglePixelPackedSampleModel sampleModel)
      {
        StringBuffer buffer = new StringBuffer("");
        buffer.append(sampleModel.getClass().getName());
        buffer.append("[width: ");
        buffer.append(sampleModel.getWidth());
        buffer.append(", height: ");
        buffer.append(sampleModel.getHeight());
        buffer.append(", numBands: ");
        buffer.append(sampleModel.getNumBands());
        buffer.append(", dataType: ");
        buffer.append(sampleModel.getDataType());
        buffer.append(", scanlineStride: ");
        buffer.append(sampleModel.getScanlineStride());
        buffer.append(", transferType: ");
        buffer.append(sampleModel.getTransferType());
        buffer.append(", numDataElements: ");
        buffer.append(sampleModel.getNumDataElements());
        buffer.append(", bitMasks: ");
        buffer.append(toString(sampleModel.getBitMasks(), 16));
        buffer.append(", bitOffsets: ");
        buffer.append(toString(sampleModel.getBitOffsets(), 10));
        buffer.append("]");

        return buffer.toString();
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    private static String toString (PixelInterleavedSampleModel sampleModel)
      {
        StringBuffer buffer = new StringBuffer("");
        buffer.append(sampleModel.getClass().getName());
        buffer.append("[width: ");
        buffer.append(sampleModel.getWidth());
        buffer.append(", height: ");
        buffer.append(sampleModel.getHeight());
        buffer.append(", numBands: ");
        buffer.append(sampleModel.getNumBands());
        buffer.append(", dataType: ");
        buffer.append(sampleModel.getDataType());
        buffer.append(", scanlineStride: ");
        buffer.append(sampleModel.getScanlineStride());
        buffer.append(", transferType: ");
        buffer.append(sampleModel.getTransferType());
        buffer.append(", numDataElements: ");
        buffer.append(sampleModel.getNumDataElements());
        buffer.append(", bandOffsets: ");
        buffer.append(toString(sampleModel.getBandOffsets(), 10));
        buffer.append(", bankIndices: ");
        buffer.append(toString(sampleModel.getBankIndices(), 10));
        buffer.append("]");

        return buffer.toString();
      }
    /*******************************************************************************
     *
     *
     ******************************************************************************/
    protected static ICC_Profile getICCProfile (RenderedImage image)
      {
        ColorSpace colorSpace = image.getColorModel().getColorSpace();

        if (colorSpace instanceof ICC_ColorSpace)
          {
            ICC_ColorSpace iccColorSpace = (ICC_ColorSpace)colorSpace;
            return iccColorSpace.getProfile();
          }

        return null;
      }

    /*******************************************************************************
     *
     *
     ******************************************************************************/
    protected static String getICCProfileName (ICC_Profile profile)
      {
        if (profile == null)
          return null;

        byte[] xx = profile.getData(ICC_Profile.icSigProfileDescriptionTag);
        int offset = 12;
        int count;

        for (count = 1; xx[offset + count] != 0; count++);

        return new String(xx, 0, offset, count);
      }
  }
