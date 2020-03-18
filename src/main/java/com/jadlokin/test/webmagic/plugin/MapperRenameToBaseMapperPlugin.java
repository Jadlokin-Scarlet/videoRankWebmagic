package com.jadlokin.test.webmagic.plugin;

import com.jadlokin.test.webmagic.util.MapperPackage;
import com.jadlokin.test.webmagic.util.Package;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

import java.util.List;

public class MapperRenameToBaseMapperPlugin extends PluginAdapter {

	private Package pack = null;

	@Override
	public void initialized(IntrospectedTable introspectedTable) {
		super.initialized(introspectedTable);
		String packString = introspectedTable.getMyBatis3JavaMapperType();
		String newPack = addBaseTo(packString);
		introspectedTable.setMyBatis3JavaMapperType(newPack);

		introspectedTable.setMyBatis3XmlMapperFileName(pack.getName()+"Mapper.xml");
	}

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	private String addBaseTo(String packString){
		pack = new MapperPackage(packString);
		String name = pack.getName();
		return pack.setName(name.concat("Base")).toString();
	}

}
