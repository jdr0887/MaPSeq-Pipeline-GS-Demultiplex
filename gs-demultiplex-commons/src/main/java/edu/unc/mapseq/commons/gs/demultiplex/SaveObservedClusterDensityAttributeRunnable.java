package edu.unc.mapseq.commons.gs.demultiplex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unc.mapseq.dao.MaPSeqDAOBeanService;
import edu.unc.mapseq.dao.MaPSeqDAOException;
import edu.unc.mapseq.dao.model.Attribute;
import edu.unc.mapseq.dao.model.Flowcell;
import edu.unc.mapseq.dao.model.Sample;

public class SaveObservedClusterDensityAttributeRunnable implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(SaveObservedClusterDensityAttributeRunnable.class);

    private MaPSeqDAOBeanService mapseqDAOBeanService;

    private Flowcell flowcell;

    public SaveObservedClusterDensityAttributeRunnable(MaPSeqDAOBeanService mapseqDAOBeanService, Flowcell flowcell) {
        super();
        this.mapseqDAOBeanService = mapseqDAOBeanService;
        this.flowcell = flowcell;
    }

    @Override
    public void run() {
        logger.debug("ENTERING run()");

        try {

            File flowcellDir = new File(flowcell.getBaseDirectory(), flowcell.getName());
            File dataDir = new File(flowcellDir, "Data");
            File reportsDir = new File(dataDir, "reports");

            if (!reportsDir.exists()) {
                logger.warn("reportsDir does not exist: {}", reportsDir.getAbsolutePath());
                return;
            }

            File numClustersByLaneFile = new File(reportsDir, "NumClusters By Lane.txt");
            logger.info("numClustersByLaneFile = {}", numClustersByLaneFile.getAbsolutePath());
            if (!numClustersByLaneFile.exists()) {
                logger.warn("numClustersByLaneFile does not exist");
                return;
            }

            List<Sample> sampleList = mapseqDAOBeanService.getSampleDAO().findByFlowcellId(flowcell.getId());
            Map<Integer, List<Double>> laneClusterDensityTotalMap = new HashMap<Integer, List<Double>>();

            for (Sample sample : sampleList) {
                if (!laneClusterDensityTotalMap.containsKey(sample.getLaneIndex())) {
                    laneClusterDensityTotalMap.put(sample.getLaneIndex(), new ArrayList<Double>());
                }
            }

            BufferedReader br = new BufferedReader(new FileReader(numClustersByLaneFile));
            // skip the first 11 lines
            for (int i = 0; i < 11; ++i) {
                br.readLine();
            }
            String line;
            while ((line = br.readLine()) != null) {
                Integer lane = Integer.valueOf(StringUtils.split(line)[0]);
                Double clusterDensity = Double.valueOf(StringUtils.split(line)[2]);
                if (laneClusterDensityTotalMap.containsKey(lane + 1)) {
                    laneClusterDensityTotalMap.get(lane + 1).add(clusterDensity);
                }
            }
            br.close();

            for (Sample sample : sampleList) {
                List<Double> laneClusterDensityTotalList = laneClusterDensityTotalMap.get(sample.getLaneIndex());
                long clusterDensityTotal = 0;
                for (Double clusterDensity : laneClusterDensityTotalList) {
                    clusterDensityTotal += clusterDensity;
                }
                String value = (double) (clusterDensityTotal / laneClusterDensityTotalList.size()) / 1000 + "";
                logger.info("value = {}", value);

                Set<Attribute> attributeSet = sample.getAttributes();

                Set<String> entityAttributeNameSet = new HashSet<String>();

                if (!attributeSet.isEmpty()) {
                    for (Attribute attribute : attributeSet) {
                        entityAttributeNameSet.add(attribute.getName());
                    }
                }

                Set<String> synchSet = Collections.synchronizedSet(entityAttributeNameSet);

                if (StringUtils.isNotEmpty(value)) {
                    if (synchSet.contains("observedClusterDensity")) {
                        for (Attribute attribute : attributeSet) {
                            if (attribute.getName().equals("observedClusterDensity")) {
                                attribute.setValue(value);
                                mapseqDAOBeanService.getAttributeDAO().save(attribute);
                                break;
                            }
                        }
                    } else {
                        Attribute attribute = new Attribute("observedClusterDensity", value);
                        attribute.setId(mapseqDAOBeanService.getAttributeDAO().save(attribute));
                        attributeSet.add(attribute);
                    }
                }
                sample.setAttributes(attributeSet);
                mapseqDAOBeanService.getSampleDAO().save(sample);
            }

        } catch (NumberFormatException | MaPSeqDAOException | IOException e) {
            e.printStackTrace();
        }
    }

}
