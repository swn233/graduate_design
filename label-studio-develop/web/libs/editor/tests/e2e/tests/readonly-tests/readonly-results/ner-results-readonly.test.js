Feature("Readonly Annotation");

const imageExamples = new DataTable(["example", "regionName"]);

imageExamples.add([require("../../../examples/text-html"), "Date"]);

Data(imageExamples).Scenario("NER Readonly Results", async ({ I, current, LabelStudio, AtOutliner }) => {
  I.amOnPage("/");
  const { config, result: r, data } = current.example;

  // mark first result as readonly
  const result = r.map((r, i) => (i === 0 ? { ...r, readonly: true } : r));

  // extracts label regions only
  const regions = result.filter((r) => r.type.match("labels"));

  LabelStudio.init({
    annotations: [
      {
        id: "test",
        result,
      },
    ],
    config,
    data,
  });

  I.say("Check region is selectable");
  AtOutliner.seeRegions(regions.length);

  I.say("Attempt to delete a readonly region");
  AtOutliner.clickRegion("Date");
  I.pressKey("Backspace");
  I.say("Results are equal after deletion attempt");
  await LabelStudio.resultsNotChanged(result);

  // Person
  I.say("Attempt to delete a non-readonly region");
  AtOutliner.clickRegion("Person");
  I.pressKey("Backspace");
  I.say("Results are not equal after deletion attempt");
  await LabelStudio.resultsChanged(result);
  I.pressKey("CommandOrControl+z");

  I.say("Can draw new shape");
  I.pressKey("1");

  AtOutliner.seeRegions(regions.length);
});
