<?php

require_once 'Zend/Controller/Action.php';
class MsgModel extends Zend_Db_Table_Abstract {

    /**
     * The default table name
     */
    protected $_name = 'msg';
    protected $_primary = 'mid';
    
    public function getNewMsg($uid){
    	$result = $this->_db->fetchAll("select * from msg,user where rcverid = $uid and senderid = uid and msg.status = " . MsgModel::UnRead );
    	$maxmid = 0;
    	foreach($result as $r){
    		if($r['mid'] > $maxmid)
    			$maxmid = $r['mid'];
    	}
    	$this->update(array('status' => MsgModel::Readed), "mid <= $maxmid and rcverid = $uid");
    	return $result;
    }
    
    public function setNewsReaded($mid){
    	
    }
    
    const UnRead = 0;
    const Readed = 1;
}

?>