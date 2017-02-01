package se.vgregion.ifeedpoc.service;


import se.vgregion.ifeedpoc.model.UserDetail;
import se.vgregion.ifeedpoc.model.UserList;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

public interface UserService {

    UserList getPortalUserList(int startIndex, int limit) throws SystemException;

    UserDetail getPortalUserDetail(long userId) throws SystemException, PortalException;

}
