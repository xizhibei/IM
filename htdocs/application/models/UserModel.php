<?php

/**
 * UserModel
 *  
 * @author X
 * @version 
 */
require_once 'Zend/Db/Table/Abstract.php';

class UserModel extends Zend_Db_Table_Abstract{

    /**
     * The default table name
     */
    protected $_name = 'user';
    protected $_primary = 'uid';
    
    public static function ConvertUser($user){
        if (!isset($user['uid']))
            return null;
        if ($user ['sex'] == 0) {
            $user ['sex'] = "Unknow";
        } else if ($user ['sex'] == 1) {
            $user ['sex'] = "Male";
        } else
            $user ['sex'] = "Female";

        $h = time() - $user ['locupdatetime'];
        if ($h / 86400 >= 1)
            $user ['locupdatetime'] = intval($h / 86400) . "天前";
        else if ($h / 3600 >= 1)
            $user ['locupdatetime'] = intval($h / 3600) . "小时前";
        else
            $user ['locupdatetime'] = intval($h / 60) . "分钟前";

        $user ['regdate'] = date("Y-m-d", $user ['regdate']);
        $user ['age'] = intval((time() - $user['birthday']) / (365.25 * 86400));
        return $user;
    }

    public function GetUser($uid) {
        $p = $this->_db->fetchRow("select * from user left join location on user.lid = location.lid where user.uid = $uid");
        return UserModel::ConvertUser($p);
    }

//    public function GetUserLoc($uid) {
//        return $this->_db->fetchRow("select latitude,longitude from user,location where user.lid = location.uid and uid = " . $uid);
//    }

    public function EmailExist($email) {
        $where = $this->_db->quoteInto("email = ?", $email);
        $result = $this->_db->fetchOne("select count(*) from user where " . $where);
        if ($result > 0)
            return true;
        else
            return false;
    }

    public function EncryptPwd($pwd, $salt) {
        return md5($pwd . $salt);
    }

    public function Validate($email, $pwd) {
        $result = $this->fetchRow($this->_db->quoteInto("email = ?", $email));
        if ($result == null)
            return - 2;
        else
            $result = $result->toArray();

        $inputpwd = $this->EncryptPwd($pwd, $result ['salt']);

        if ($inputpwd != $result ['pwd']) {
            return - 1;
        } else {
            $p = $result;
            if ($p['sex'] == 0) {
                $p['sex'] = "Unknow";
            } else if ($p['sex'] == 1) {
                $p['sex'] = "Maie";
            }else
                $p['sex'] = "Femaie";

            $h = time() - $p['locupdatetime'];
            if ($h / 86400 >= 1)
                $p['updatetime'] = intval($h / 86400) . "天前";
            else if ($h / 3600 >= 1)
                $p['updatetime'] = intval($h / 3600) . "小时前";
            else
                $p['updatetime'] = intval($h / 60) . "分钟前";

            $p['regdate'] = date("Y-m-d", $p['regdate']);
            $p['age'] = intval((time() - $p['birthday']) / (365.25 * 86400));
            return $p;
        }
        // $result = $this->_db->fetchOne("select count(*) from user where email
        // = $email and pwd = $pwd");
        // if($result > 0)
        // return true;
        // else
        // return false;
    }

    public function Search($name) {
        
    }

    const EmailNotValidate = 0;
    const Normal = 1;
    const SelfDeleted = 2;
    const Locked = 3;
    const AdminLocked = 4;

    public function GetStatus($id) {
        
    }

    public function GetStatusId($status) {
        
    }

}
