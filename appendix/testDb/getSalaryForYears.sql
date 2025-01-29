select sum(hours*price) from months
left join days 
on (months.id = days.monthId)
where months.yearId = 1