package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.Folder;
import uz.uzkassa.cts.dto.document.FolderDTO;
import uz.uzkassa.cts.dto.document.FolderTreeDTO;

import java.util.List;

public interface FolderService extends BaseService<Folder, Long> {

    List<FolderTreeDTO> getTree();

    Long create(FolderDTO folderDTO);

    Long update(Long id, FolderDTO folderDTO);
}
