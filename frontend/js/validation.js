const required = { Leave: ['leave_army', 'leave_card', 'leave_rank', 'leave_name'], 'TD / Posting': ['td_army', 'td_card', 'td_rank', 'td_name'], 'Vehicle In / Out': ['veh_no', 'veh_name'], Outpass: ['out_id', 'out_name'], 'Night Pass': ['night_id', 'night_name'] };
export function validateForm(module, data) {
	if (!required[module] || required[module].some((id) => !String(data[id] || '').trim())) { alert('Please fill the required fields.'); return false; }
	const dateIds = { Leave:['leave_dt'], 'TD / Posting':['td_dt'], 'Vehicle In / Out':['veh_dt'], Outpass:['out_dt','out_return'], 'Night Pass':['night_dt','night_return'] }[module];
	if (dateIds.some((id) => data[id] && Number.isNaN(new Date(data[id]).getTime()))) { alert('Please enter a valid date and time.'); return false; }
	if (dateIds.length > 1 && data[dateIds[0]] && data[dateIds[1]] && new Date(data[dateIds[1]]) < new Date(data[dateIds[0]])) { alert('Return time cannot be earlier than departure time.'); return false; }
	return true;
}
