package org.apache.samoa.topology.impl.gearpump;

import io.gearpump.Message;
import io.gearpump.partitioner.*;
import scala.collection.immutable.*;

public class SamoaMessagePartitioner implements MulticastPartitioner {
  ShufflePartitioner shufflePartitioner = new ShufflePartitioner();
  BroadcastPartitioner broadcastPartitioner = new BroadcastPartitioner();
  HashPartitioner hashPartitioner = new HashPartitioner();

  @Override
  public List<Object> getPartitions(Message msg, int partitionNum, int currentPartitionId) {
    GearpumpMessage message = (GearpumpMessage) msg.msg();
    Integer partition = -1;
    List result = null;
    switch (message.getScheme()) {
      case SHUFFLE:
        partition = shufflePartitioner.getPartition(msg, partitionNum, currentPartitionId);
        result = $colon$colon$.MODULE$.apply(partition, (List)Nil$.MODULE$);
        break;
      case BROADCAST:
        result = broadcastPartitioner.getPartitions(msg, partitionNum);
        break;
      case GROUP_BY_KEY:
        //Todo replace HashPartitioner
        partition = hashPartitioner.getPartition(msg, partitionNum, currentPartitionId);
        result = $colon$colon$.MODULE$.apply(partition, (List)Nil$.MODULE$);
        break;
    }
    return result;
  }

  @Override
  public List<Object> getPartitions(Message msg, int partitionNum) {
    return this.getPartitions(msg, partitionNum, -1);
  }
}
