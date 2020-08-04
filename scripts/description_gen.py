# coding=utf-8

import os,json

class Generator:

	assets = "../app/src/main/assets/"

	def findCategory(self, id):
		for category in self.categories:
			if (int(id) == int(category["id"])):
				 return category["title"]
		return "-"


	def generate(self, lang, langFolder):
		path = self.assets + lang
		draft_path = "desc_" + lang.lower() + ".txt"

		files = os.listdir(path)
		files.sort()
		self.categories = json.loads(open(path + "/categories.json").read())
		draft = open(draft_path).read()
		target_path = "../fastlane/metadata/android/" + langFolder +"/full_description.txt"


		out = u""
		category_string = u""
		for f in files:
			if (f != "categories.json"):
				category = f[0:f.index(".")]
				out += "<b>" + self.findCategory(category) + "</b>\n"
				category_string += self.findCategory(category) + ", "
				content = open(path + "/" + f, "r").read()
				sources = json.loads(content)
				for source in sources:
					out += "- " + source["n"] + "\n"
				out += "\n"

		new = draft.replace("{{sources}}", out.encode("utf-8")).replace("{{categories}}", category_string[0:-2].encode("utf-8"))
		out_file = open(target_path, "w")
		out_file.write(new)
		out_file.close()


def __main__():
	langs = {"tr":"tr-TR","de":"de-DE","us":"en-US","nl":"nl-NL"}
	gen = Generator()
	for lang in langs:
		gen.generate(lang, langs[lang])


__main__()