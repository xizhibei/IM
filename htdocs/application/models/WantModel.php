<?php

/**
 * FriendModel
 *  
 * @author X
 * @version 
 */

require_once 'Zend/Db/Table/Abstract.php';


class WantModel extends Zend_Db_Table_Abstract {
	/**
	 * The default table name
	 */
	protected $_name = 'want';
	
        const Normal = 0;
        const Deleted = 1;
        const Expired = 2;
        
        public function getHistory($uid){
            $result = $this->_db->fetchAll("select * from want,activity where want.acid = activity.aid and uid = $uid");
            foreach($result as &$r){
                $h = time() - $r ['d_updatetime'];
                if ($h / 86400 >= 1)
                    $r ['d_updatetime'] = intval($h / 86400) . "天前";
                else if ($h / 3600 >= 1)
                    $r ['d_updatetime'] = intval($h / 3600) . "小时前";
                else
                    $r ['d_updatetime'] = intval($h / 60) . "分钟前";
            }
            return $result;
        }
}
