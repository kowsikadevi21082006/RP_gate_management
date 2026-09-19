export const state = { records: [], people: [], lastSave: null };
export function replaceRecords(records) { state.records = Array.isArray(records) ? records : []; }
export function replacePeople(people) { state.people = Array.isArray(people) ? people : []; }
