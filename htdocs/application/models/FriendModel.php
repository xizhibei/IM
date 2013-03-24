<?php

/**
 * FriendModel
 *  
 * @author X
 * @version 
 */
require_once 'Zend/Db/Table/Abstract.php';

class FriendModel extends Zend_Db_Table_Abstract {

    const NONE = 16;
    const Me = 0;
    const BeFollowed = 1;
    const Fans = 2;
    const Friend = 3;
    const BLACK_LIST = 4;
    const NEARBY = 8;

    protected $_name = 'friend';

    public function getFriendType($uid1, $uid2) {
        if($uid1 == $uid2)
            return FriendModel::Me;
        if($uid1 < $uid2){
            $u1 = $uid1;
            $u2 = $uid2;
        }else{
            $u1 = $uid2;
            $u2 = $uid1;
        }
        $r = $this->_db->fetchRow("select * from {$this->_name} where uid1 = $u1 and uid2 = $u2");
        if (isset ($r['fid'])){
            if($r['time1'] == 0){
                if($uid1 == $r['uid1'])
                    return FriendModel::Fans;
                else
                    return FriendModel::BeFollowed;
            }else if($r['time2'] == 0){
                if($uid1 == $r['uid2'])
                    return FriendModel::Fans;
                else
                    return FriendModel::BeFollowed;
            }else
                return FriendModel::Friend;
        }
        else
            return FriendModel::NONE;
    }

    /**
     * in friend table uid should and must less than friendid
     *
     * @param unknown_type $me        	
     * @param unknown_type $friend        	
     * @return number
     */
    public function follow($me, $friend) {
        if ($me < $friend) {
            $uid1 = $me;
            $uid2 = $friend;
        } else {
            $uid1 = $friend;
            $uid2 = $me;
        }
        $result = $this->_db->fetchRow("select * from friend where $uid1 = uid1 and $uid2 = uid2");
        if ($result == null) {
            $insertData = array(
                'uid1' => $uid1,
                'uid2' => $uid2,
                'status' => FriendModel::Follow
            );
            if ($me == $uid1)
                $insertData ['time1'] = time();
            else
                $insertData ['time2'] = time();
            $this->insert($insertData);
        } else if ($result ['status'] == FriendModel::BeFollowed) {
            $this->update(array(
                'status' => FriendModel::Friend,
                'befollowtime' => time()
                    ), "fid = " . $result ['fid']);
        } else {
            return - 1;
        }
        return 0;
    }

    public function GetAllMy($uid) {
        $result = $this->_db->fetchAll("select * from user inner join friend on 
            (uid = uid1 and uid2 = $uid or uid = uid2  and uid1 = $uid) and friend.status >= 1 and friend.status <= 3
            left join location on user.lid = location.lid 
            left join want on user.wantid = want.did 
            left join activity on want.acid = activity.aid");
        $now = time();
        foreach ($result as &$p) {
            if ($p ['sex'] == 0) {
                $p ['sex'] = "Unknow";
            } else if ($p ['sex'] == 1) {
                $p ['sex'] = "Male";
            } else
                $p ['sex'] = "Female";

            if ($p['lid'] == 0) {
                $p ['latitude'] = - 1;
                $p ['longitude'] = - 1;
                $p ['updatetime'] = 0;
            } else {
                $h = $now - $p ['locupdatetime'];
                if ($h / 86400 >= 1)
                    $p ['locupdatetime'] = intval($h / 86400) . "天前";
                else if ($h / 3600 >= 1)
                    $p ['locupdatetime'] = intval($h / 3600) . "小时前";
                else
                    $p ['locupdatetime'] = intval($h / 60) . "分钟前";
            }
            $p ['regdate'] = date("Y-m-d", $p ['regdate']);
            $p ['age'] = intval((time() - $p['birthday']) / (365.25 * 86400));
            if($p['time1'] == 0){
                if($uid == $p['uid1'])
                    $p['type'] = FriendModel::Fans;
                else
                    $p['type'] = FriendModel::BeFollowed;
            }else if($p['time2'] == 0){
                if($uid == $p['uid2'])
                    $p['type'] = FriendModel::Fans;
                else
                    $p['type'] = FriendModel::BeFollowed;
            }else
                $p['type'] = FriendModel::Friend;
            
            if ($p['wantid'] != 0) {
                $h = time() - $p ['d_updatetime'];
                if ($h / 86400 >= 1)
                    $p ['d_updatetime'] = intval($h / 86400) . "天前";
                else if ($h / 3600 >= 1)
                    $p ['d_updatetime'] = intval($h / 3600) . "小时前";
                else
                    $p ['d_updatetime'] = intval($h / 60) . "分钟前";
            }
        }
        return $result;
    }

    public function _GetAllMy($uid) {
        $result = $this->_db->fetchAll("select * from (user inner join friend on (uid = uid1 or uid = uid2) and uid <> $uid) " . 
                "left outer join location on (user.uid = location.uid) " . 
                "where ($uid = uid1 or $uid = uid2) and (lid is null or " . 
                "lid in (select lid from location group by uid having max(updatetime))) and " . 
                "friend.status >= 1 and friend.status <= 3");
        foreach ($result as &$p) {
            if ($p ['sex'] == 0) {
                $p ['sex'] = "Unknow";
            } else if ($p ['sex'] == 1) {
                $p ['sex'] = "Maie";
            } else
                $p ['sex'] = "Femaie";
            if ($p ['updatetime'] == null)
                $p ['updatetime'] = 0;
            else {
                $h = time() - $p ['updatetime'];
                if ($h / 86400 >= 1)
                    $p ['updatetime'] = intval($h / 86400) . "天前";
                else if ($h / 3600 >= 1)
                    $p ['updatetime'] = intval($h / 3600) . "小时前";
                else
                    $p ['updatetime'] = intval($h / 60) . "分钟前";
            }
            if ($p ['latitude'] == null) {
                $p ['latitude'] = - 1;
                $p ['longitude'] = - 1;
            }
            $p ['regdate'] = date("Y-m-d", $p ['regdate']);
        }
        return $result;
    }

}
