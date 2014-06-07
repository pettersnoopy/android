package org.vudroid.djvudroid;

import org.vudroid.core.BaseViewerFragment;
import org.vudroid.core.DecodeService;
import org.vudroid.core.DecodeServiceBase;
import org.vudroid.djvudroid.codec.DjvuContext;

public class DjvuViewerFragment extends BaseViewerFragment
{
    @Override
    protected DecodeService createDecodeService()
    {
        return new DecodeServiceBase(new DjvuContext());
    }
}
