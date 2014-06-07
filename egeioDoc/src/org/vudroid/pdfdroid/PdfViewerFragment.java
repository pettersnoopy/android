package org.vudroid.pdfdroid;

import org.vudroid.core.BaseViewerFragment;
import org.vudroid.core.DecodeService;
import org.vudroid.core.DecodeServiceBase;
import org.vudroid.pdfdroid.codec.PdfContext;

public class PdfViewerFragment extends BaseViewerFragment
{
    @Override
    protected DecodeService createDecodeService()
    {
        return new DecodeServiceBase(new PdfContext());
    }
}
