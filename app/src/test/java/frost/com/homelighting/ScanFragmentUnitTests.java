package frost.com.homelighting;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import frost.com.homelighting.db.entity.DeviceEntity;
import frost.com.homelighting.ui.scan.ScanFragment;
import frost.com.homelighting.viewmodel.ScanViewModel;

public class ScanFragmentUnitTests {

    private static final String TAG = "SCAN_FRAGMENT_TEST";

    private ScanFragment scanFragment;

    @Mock
    private Repository mockRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this); // required for @Mock annotations

        mockRepository = createMockRepository();
        // Make Scan View Model a mock while using a mock repository
        scanFragment = Mockito.spy(new ScanFragment());
    }

    @Test
    public void getOnlineDevices_noOnlineDevices(){
        // Trigger
        List<DeviceEntity> deviceEntities = scanFragment.
        // Validation
    }

    private Repository createMockRepository(){
        when(mockRepository.loadOnlineDevices()).thenReturn(null);

        return mockRepository;
    }
}
