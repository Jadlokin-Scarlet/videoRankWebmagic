package com.jadlokin.test.webmagic.util.object;

import com.jadlokin.test.webmagic.util.Lists;

import java.util.List;

public class Package {
	private List<String> packages;

	public Package(String pack){
		packages = Lists.of(pack.split("\\."));
	}

	@Override
	public String toString(){
		return packages.stream()
				.reduce((a,b) -> a.concat(".").concat(b))
				.orElse("");
	}

	public Package setName(String className){
		packages.set(getClassNameIndex(), className);
		return this;
	}

	public String getName(){
		return packages.get(getClassNameIndex());
	}

	private int getClassNameIndex(){
		return packages.size() - 1;
	}
}
