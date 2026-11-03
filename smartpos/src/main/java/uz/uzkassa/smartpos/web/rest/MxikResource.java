package uz.uzkassa.smartpos.web.rest;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.nic.MxikDTO;
import uz.uzkassa.smartpos.integration.tasnif.dto.MxikPackage;
import uz.uzkassa.smartpos.service.MxikService;

import java.util.List;

@RestController
@RequestMapping("/api/mxik")
@AllArgsConstructor
public class MxikResource {
    private final MxikService mxikService;

    @GetMapping("/add")
    public List<MxikDTO> add(@RequestParam List<String> mxiks) {
        return mxikService.add(mxiks);
    }

    @GetMapping(value = "/{mxikCode}/package")
    public List<MxikPackage> getLocalPackages(@PathVariable String mxikCode) {
        return mxikService.getPackagesByMxik(mxikCode);
    }

    @GetMapping(value = "/{mxikCode}/package/sync")
    public List<MxikPackage> syncLocalPackages(@PathVariable String mxikCode) {
        return mxikService.partialSync(mxikCode);
    }

    @GetMapping("/sync/full")
    public void fullSync() {
        mxikService.fullTimeSync();
    }

    @GetMapping("/sync/details")
    public void syncPackages() {
        mxikService.syncDetails();
    }

    @GetMapping("/sync/packages/basic")
    public void syncBasicPackages() {
        mxikService.syncBasicMxikPackages();
    }
}
