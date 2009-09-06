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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import it.tidalwave.imageio.util.Logger;
import it.tidalwave.imageio.io.RAWImageInputStream;
import it.tidalwave.imageio.raw.Directory;
import it.tidalwave.imageio.raw.RAWImageReaderSupport;
import it.tidalwave.imageio.raw.RAWMetadataSupport;
import it.tidalwave.imageio.tiff.IFD;
import it.tidalwave.imageio.tiff.TIFFImageReaderSupport;
import it.tidalwave.imageio.tiff.ThumbnailLoader;

/***********************************************************************************************************************
 *
 * @author  Fabrizio Giudici
 * @version $Id$
 *
 **********************************************************************************************************************/
public class CRWImageReader extends RAWImageReaderSupport
  {
    private final static String CLASS = CRWImageReader.class.getName();
    private final static Logger logger = Logger.getLogger(CLASS);

    /** The thumbnail count. */
//    private int thumbnailCount;

    /** True if is available a JPG thumbnail. */
    private boolean jpgFromRawAvailable;

    private boolean thumbnailImageAvailable;

    /** The image IFD. */
    private IFD imageIFD;

    /** The image IFD. */
    private IFD exifIFD;

    /** The CRW Maker Note. */
    private CanonCRWMakerNote canonMakerNote;

    private final List<ThumbnailLoader> thumbnailLoaders = new ArrayList<ThumbnailLoader>();

    /*******************************************************************************************************************
     *
     ******************************************************************************************************************/
    protected CRWImageReader (final @Nonnull ImageReaderSpi originatingProvider)
      {
        super(originatingProvider);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    public int getNumThumbnails (final @Nonnegative int imageIndex)
      throws IOException
      {
        checkImageIndex(imageIndex);
        ensureMetadataIsLoaded(imageIndex);
        return thumbnailLoaders.size();
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Nonnegative
    public int getWidth (final @Nonnegative int imageIndex)
      throws IOException
      {
        checkImageIndex(imageIndex);
        ensureMetadataIsLoaded(imageIndex);
        return ((CRWMetadata)metadata).getWidth();
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Nonnegative
    public int getHeight (final @Nonnegative int imageIndex)
      throws IOException
      {
        checkImageIndex(imageIndex);
        ensureMetadataIsLoaded(imageIndex);
        return ((CRWMetadata)metadata).getHeight();
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    @Nonnegative
    public int getThumbnailWidth (final @Nonnegative int imageIndex, final @Nonnegative int thumbnailIndex)
      throws IOException
      {
        checkImageIndex(imageIndex);
        ensureMetadataIsLoaded(imageIndex);
        checkThumbnailIndex(thumbnailIndex);
        return thumbnailLoaders.get(thumbnailIndex).getWidth();
//        return ((CRWMetadata)metadata).getThumbnailWidth(thumbnailIndex);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Override
    @Nonnegative
    public int getThumbnailHeight (final @Nonnegative int imageIndex, final @Nonnegative int thumbnailIndex)
      throws IOException
      {
        checkImageIndex(imageIndex);
        ensureMetadataIsLoaded(imageIndex);
        checkThumbnailIndex(thumbnailIndex);
        return thumbnailLoaders.get(thumbnailIndex).getHeight();
//        return ((CRWMetadata)metadata).getThumbnailHeight(thumbnailIndex);
      }

    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    @Nonnull
    protected Directory loadPrimaryDirectory() 
      throws IOException
      {
        logger.info("loadPrimaryDirectory() - iis: %s", iis);
        long directoryOffset = processHeader(iis, true);
        primaryDirectory = new CanonCRWMakerNote();
        primaryDirectory.loadAll(iis, directoryOffset);

        return primaryDirectory;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Nonnull
    protected BufferedImage loadThumbnail (final @Nonnegative int imageIndex, final @Nonnegative int thumbnailIndex)
      throws IOException
      {
        logger.fine("loadThumbnail(%d, %d) - iis: %s", imageIndex, thumbnailIndex, iis);
        checkImageIndex(imageIndex);
        ensureMetadataIsLoaded(imageIndex);
        checkThumbnailIndex(thumbnailIndex);

        return thumbnailLoaders.get(thumbnailIndex).load(iis);
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    @Nonnull
    protected WritableRaster loadRAWRaster() 
      throws IOException
      {
        logger.fine("loadRAWRaster() - iis: %s", iis);

        long time = System.currentTimeMillis();
        CRWMetadata crwMetadata = ((CRWMetadata)metadata);
        CRWRasterReader rasterReader = CRWRasterReader.getInstance(crwMetadata.getModel());
        rasterReader.setWidth(crwMetadata.getSensorWidth());
        rasterReader.setHeight(crwMetadata.getSensorHeight());
        rasterReader.setBitsPerSample(12); // FIXME - get from the model
        rasterReader.setCFAPattern(new byte[] { 0, 1, 1, 2 }); // FIXME RGGB - gets from the model
        int[] decoderTable = crwMetadata.getDecoderTable();
        rasterReader.setRasterOffset(decoderTable[2]);
        rasterReader.setDecoderPairIndex(decoderTable[0]);
        rasterReader.setStripByteCount(1);
        rasterReader.setCompression(0);
        logger.fine(">>>> using RasterReader: %s", rasterReader);

        WritableRaster raster = rasterReader.loadRaster(iis, this);
        logger.fine(">>>> loadRAWRaster() completed ok in %d msec", (System.currentTimeMillis() - time));

        return raster;
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    protected void checkThumbnailIndex (final @Nonnegative int thumbnailIndex)
      {
        if (thumbnailIndex >= thumbnailLoaders.size())
          {
            throw new IndexOutOfBoundsException("Invalid thumbnail index: " + thumbnailIndex);
          }
      }

    /*******************************************************************************************************************
     *
     * {@inheritDoc}
     *
     ******************************************************************************************************************/
    protected void processMetadata() 
      throws IOException
      {
        logger.fine("processMetadata()");
        primaryDirectory = loadPrimaryDirectory();
        logger.finer(">>>> primary directory: %s", primaryDirectory);

        final CanonCRWMakerNote crwMakerNote = ((CanonCRWMakerNote)primaryDirectory);
        thumbnailImageAvailable = crwMakerNote.isThumbnailImageAvailable();
        jpgFromRawAvailable = crwMakerNote.isJpgFromRawAvailable();

        if (thumbnailImageAvailable)
          {
            logger.finest(">>>> thumbnailImageAvailable");
            final CIFFTag thumbTag = (CIFFTag)crwMakerNote.getTag(CanonCRWMakerNote.THUMBNAIL_IMAGE);
            final int jpegOffset = thumbTag.getOffset() + thumbTag.getBaseOffset();
            final int jpegSize = thumbTag.getSize();
            thumbnailLoaders.add(new ThumbnailLoader(iis, jpegOffset, jpegSize));
          }

        if (jpgFromRawAvailable)
          {
            logger.finest(">>>> jpgFromRawAvailable");
            final CIFFTag jpgTag = (CIFFTag)crwMakerNote.getTag(CanonCRWMakerNote.JPG_FROM_RAW);
            final int jpegOffset = jpgTag.getOffset() + jpgTag.getBaseOffset();
            final int jpegSize = jpgTag.getSize();
            thumbnailLoaders.add(new ThumbnailLoader(iis, jpegOffset, jpegSize));
          }

        Collections.sort(thumbnailLoaders);
        logger.finer(">>>> thumbnailLoaders: %s", thumbnailLoaders);
        tryToReadEXIFFromTHM();
        metadata = createMetadata(primaryDirectory, imageIFD);
        logger.finer(">>>> metadata: %s", metadata);
      }

    /*******************************************************************************************************************
     * 
     * Releases all the allocated resources.
     * 
     ******************************************************************************************************************/
    @Override
    protected void disposeAll()
      {
        super.disposeAll();
        exifIFD = null;
        imageIFD = null;
      }

    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    @Override
    @Nonnull
    protected Object wrapInput (final @Nonnull Object input)
      {
        // FIXME: should use the superclass to check if input is a good object
        try
          {
            return new CRWImageInputStream((ImageInputStream)input);
          }
        catch (IOException e)
          {
            throw new RuntimeException(e);
          }
      }

    /*******************************************************************************************************************
     * 
     * @throws IOException
     * 
     ******************************************************************************************************************/
    private void tryToReadEXIFFromTHM()
      throws IOException
      {
        CRWImageInputStream iis = (CRWImageInputStream)this.iis;
        long fileOffsetSave = iis.getBaseOffset(); // FIXME: terrific kludge!
        //
        // This must be cleaned up, in particular by removing the dep upon CRWImageInputStream.
        // The switch from-to THM must be performed by just seeking past the end of the
        // CRW section; at this point the CRWImageInputStream should make it available the .THM.
        // mark() and reset() should be used to revert back to the .CRW file.
        //
        try
          {
            if (iis.switchToTHMStream())
              {
                iis.setBaseOffset(12); // FIXME: where does this come from?
                iis.seek(0);
                long directoryOffset = TIFFImageReaderSupport.processHeader(iis, null);
                imageIFD = new IFD();
                imageIFD.loadAll(iis, directoryOffset);
                logger.finer("THM PRIMARY IFD: %s", imageIFD);
                processEXIFAndMakerNote(imageIFD, iis);
              }
          }

        finally
          {
            iis.switchToCRWStream();
            iis.setBaseOffset(fileOffsetSave);
          }
      }

    /*******************************************************************************************************************
     *
     * Processes the EXIF metadata, if present. The EXIF data is added to the
     * imageMetadata. The MakerNote is processed too.
     *
     * @param   directory    the primary directory
     * @param   iis          the image input stream
     * @throws  IOException  if an I/O error occurs
     *
     ******************************************************************************************************************/
    protected void processEXIFAndMakerNote (final @Nonnull Directory directory,
                                            final @Nonnull RAWImageInputStream iis)
      throws IOException
      {
        if (((IFD)directory).isExifIFDPointerAvailable())
          {
            exifIFD = new IFD();
            exifIFD.loadAll(iis, ((IFD)directory).getExifIFDPointer());
            imageIFD.addNamedDirectory(IFD.EXIF_NAME, exifIFD);
            logger.fine("EXIF IFD: %s", exifIFD);

            if (exifIFD.isMakerNoteAvailable())
              {
                processMakerNote(iis);
              }

                       if (exifIFD.isInteroperabilityIFDAvailable())
                         {
                           IFD interoperabilityIFD = new IFD();
                           interoperabilityIFD.loadAll(iis, exifIFD.getInteroperabilityIFD());
                           logger.fine("Interoperability IFD: %s", interoperabilityIFD);
                           // TODO: add to EXIF IFD
                         }
            //               interoperabilityIFD = (IFD)Directory.loadDirectory(iis, exifIFD.getInteroperabilityIFD(), fileOffset, IFD.class);
            //               exifIFD.addNamedDirectory("INTEROP", interoperabilityIFD);
            //               logger.fine("INTEROPERABILITY IFD: " + interoperabilityIFD);
            //             }
          }
        //
        //       if (((IFD)directory).isGPSInfoIFDPointerAvailable())
        //         {
        //           gpsIFD = (IFD)Directory.loadDirectory(iis, ((IFD)directory).getGPSInfoIFDPointer(), fileOffset, IFD.class);
        //           imageIFD.addNamedDirectory("GPS", gpsIFD); // FIXME
        //           logger.fine("GPS IFD: " + gpsIFD);
        //         }
      }

    /*******************************************************************************************************************
     * 
     * {@inheritDoc}
     * 
     ******************************************************************************************************************/
    @Nonnull
    protected RAWMetadataSupport createMetadata (final @Nonnull Directory primaryDirectory,
                                                 final @Nonnull Directory imageDirector)
      {
        return new CRWMetadata((CanonCRWMakerNote)primaryDirectory, imageDirector, iis, headerProcessor);
      }

    /*******************************************************************************************************************
     *
     * Processes the maker note.
     * FIXME: try to merge with super implementation.
     * 
     * @param   iis          the ImageInputStream
     * @throws  IOException  if an I/O error occurs
     *
     ******************************************************************************************************************/
    protected void processMakerNote (final @Nonnull RAWImageInputStream iis)
      throws IOException
      {
        int makerNoteOffset = exifIFD.getMakerNoteOffset();
        makerNote = new CanonCRWMakerNote();
        makerNote.load(iis, makerNoteOffset);
        exifIFD.addNamedDirectory(IFD.MAKER_NOTE_NAME, makerNote);
        logger.fine("MakerNote: %s", makerNote);
      }

    /*******************************************************************************************************************
     * 
     * @param iis
     * @param reset
     * @return
     * @throws IOException
     * 
     ******************************************************************************************************************/
    @Nonnegative
    private static long processHeader (final @Nonnull ImageInputStream iis, boolean reset)
      throws IOException
      {
        logger.fine("processHeader(iis=%s, reset=%s)", iis, reset);

        if (reset)
          {
            iis.seek(0);
          }

        logger.finest(">>>> reading byte order at %d", iis.getStreamPosition());
        TIFFImageReaderSupport.setByteOrder(iis);
        long offset = iis.readUnsignedInt();
        logger.finer(">>>> processHeader() returning offset is %d", offset);

        return offset;
      }
  }
