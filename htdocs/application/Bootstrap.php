<?php
Zend_Loader::loadClass('My_Plugin_Acl');
class Bootstrap extends Zend_Application_Bootstrap_Bootstrap {

    protected function _initAcl() {
        $auth = Zend_Auth::getInstance();
//        if (!$auth->hasIdentity()) {
//            $auth->getStorage()->write((object) array(
//                        'role' => 'guest',
//                        'name' => '游客',
//            ));
//        }

        $acl = new Zend_Acl();
        $acl->addRole(new Zend_Acl_Role('guest'));
        $acl->addRole(new Zend_Acl_Role('user'), 'guest');
        $acl->addRole(new Zend_Acl_Role('admin'));
        
        $acl->allow();

        $front = Zend_Controller_Front::getInstance();
        $tmp = new My_Plugin_Acl($auth, $acl);
        Zend_Registry::set("acl", $tmp);
        $front->registerPlugin($tmp);
    }

}

