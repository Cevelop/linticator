#include <string>

struct Class {
	std::string couldHaveConstRefParameter(std::string& s) const {
		return s;
	}
};
