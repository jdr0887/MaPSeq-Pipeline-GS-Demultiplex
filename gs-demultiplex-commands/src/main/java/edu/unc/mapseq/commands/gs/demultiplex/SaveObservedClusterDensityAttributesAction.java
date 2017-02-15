package edu.unc.mapseq.commands.gs.demultiplex;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.commons.gs.demultiplex.SaveObservedClusterDensityAttributeRunnable;
import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Flowcell;

@Command(scope = "gs-demultiplex", name = "save-observed-cluster-density-attribute", description = "Save Observed Cluster Density Attribute")
@Service
public class SaveObservedClusterDensityAttributesAction implements Action {

    private final Logger logger = LoggerFactory.getLogger(SaveObservedClusterDensityAttributesAction.class);

    @Reference
    private MaPSeqDAOBeanService maPSeqDAOBeanService;

    @Argument(index = 0, name = "flowcellId", required = true, multiValued = true)
    private List<Long> flowcellIdList;

    @Override
    public Object execute() {
        logger.debug("ENTERING execute()");
        ExecutorService es = Executors.newSingleThreadExecutor();
        if (CollectionUtils.isNotEmpty(flowcellIdList)) {
            for (Long flowcellId : flowcellIdList) {
                try {
                    Flowcell flowcell = maPSeqDAOBeanService.getFlowcellDAO().findById(flowcellId);
                    SaveObservedClusterDensityAttributeRunnable runnable = new SaveObservedClusterDensityAttributeRunnable(maPSeqDAOBeanService,
                            flowcell);
                    es.submit(runnable);
                } catch (MaPSeqDAOException e) {
                    e.printStackTrace();
                }
            }
        }
        es.shutdown();
        return null;
    }

    public List<Long> getFlowcellIdList() {
        return flowcellIdList;
    }

    public void setFlowcellIdList(List<Long> flowcellIdList) {
        this.flowcellIdList = flowcellIdList;
    }

}
